#!/usr/bin/env perl
# Converts Copycats+' generated 1.21.1 NeoForge data (src/generated/resources/data) to 26.1 / Fabric formats.
# Datagen is stubbed, so this stands in for the recipe provider. Adapted from Design n' Decor's converter.
#
# Run once from the repo root, on the unconverted data: perl porting-tools/convert_data_26.pl (not safe to rerun)
#
# - recipes: ingredient objects ({"item"}/{"tag"}) -> "id" / "#tag".
# - recipes and advancements: "neoforge:conditions" with copycats:feature_enabled -> "fabric:load_conditions"
#   (FeatureEnabledCondition is registered as a Fabric resource condition).
# Loot tables, advancements and tags are otherwise in 1.21 formats that 26.1 still reads.
use strict;
use warnings;
use File::Find;
use JSON::PP;

my $json = JSON::PP->new->utf8->canonical->pretty->indent_length(2)->space_before(0);
my $data = 'src/generated/resources/data';

sub read_json {
    my $file = shift;
    open my $in, '<:raw', $file or die "$file: $!";
    local $/;
    my $d = $json->decode(<$in>);
    close $in;
    return $d;
}

sub write_json {
    my ($file, $d) = @_;
    open my $out, '>:raw', $file or die "$file: $!";
    print $out $json->encode($d);
    close $out;
}

sub files_under {
    my $dir = shift;
    my @files;
    find(sub { push @files, $File::Find::name if /\.json$/ }, $dir) if -d $dir;
    return sort @files;
}

sub ingredient {
    my $in = shift;
    return $in unless ref $in;
    if (ref $in eq 'ARRAY') {
        my @alts = map { ingredient($_) } @$in;
        return $alts[0] if @alts == 1;
        die 'alternative tags: ' . $json->encode($in) if grep { /^#/ } @alts;
        return \@alts;
    }
    return $in->{item} if exists $in->{item};
    return "#$in->{tag}" if exists $in->{tag};
    die 'unknown ingredient ' . $json->encode($in);
}

sub output {
    my $r = shift;
    return { id => $r } unless ref $r;
    my %out = (id => $r->{item} // $r->{id});
    $out{count} = $r->{count} if ($r->{count} // 1) != 1;
    $out{components} = $r->{components} if exists $r->{components};
    return \%out;
}

sub conditions {
    my $d = shift;
    my $conds = delete $d->{'neoforge:conditions'} or return;
    $d->{'fabric:load_conditions'} = [ map {
        die "unhandled condition $_->{type}" unless $_->{type} eq 'copycats:feature_enabled';
        my %c = (condition => $_->{type}, feature => $_->{feature});
        $c{invert} = $_->{invert} if $_->{invert};
        \%c;
    } @$conds ];
}

sub convert_recipe {
    my $r = shift;
    my $type = $r->{type};
    my %out = (type => $type);
    for my $k (qw(category group show_notification fabric:load_conditions)) {
        $out{$k} = $r->{$k} if exists $r->{$k};
    }
    if ($type eq 'minecraft:crafting_shaped') {
        $out{pattern} = $r->{pattern};
        $out{key} = { map { $_ => ingredient($r->{key}{$_}) } keys %{ $r->{key} } };
        $out{result} = output($r->{result});
    } elsif ($type eq 'minecraft:crafting_shapeless') {
        $out{ingredients} = [ map { ingredient($_) } @{ $r->{ingredients} } ];
        $out{result} = output($r->{result});
    } elsif ($type eq 'minecraft:stonecutting') {
        $out{ingredient} = ingredient($r->{ingredient});
        $out{result} = output($r->{result});
    } else {
        die "unsupported recipe type $type";
    }
    return \%out;
}

my %count;
for my $f (files_under("$data/copycats/recipe")) {
    my $d = read_json($f);
    conditions($d);
    write_json($f, convert_recipe($d));
    $count{recipes}++;
}
for my $f (files_under("$data/copycats/advancement")) {
    my $d = read_json($f);
    next unless conditions($d);
    write_json($f, $d);
    $count{advancements}++;
}
print "converted: " . join(', ', map { "$count{$_} $_" } sort keys %count) . "\n";
