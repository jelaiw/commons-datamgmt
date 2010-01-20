ENT - Genotype Phasing by Entropy Minimization
Version 1.0.2, Oct. 5, 2008
==============================================

A Single Nucleotide Polymorphism (SNP) is a position in the genome at
which two or more of the possible four nucleotides occur in a large
percentage of the population.  SNPs account for most of the genetic
variability between individuals, and mapping SNPs in the human population
has become the next high-priority in genomics after the completion of the
Human Genome project. In diploid organisms such as humans, there are two
non-identical copies of each autosomal chromosome. A description of the
SNPs in a chromosome is called a haplotype.

At present, it is prohibitively expensive to directly determine the
haplotypes of an individual, but it is possible to obtain rather easily
the conflated SNP information in the so called genotype. Computational
methods for genotype phasing, i.e., inferring haplotypes from genotype
data, have received much attention in recent years as haplotype
information leads to increased statistical power of disease association
tests.  ENT is a highly scalable genotype phasing algorithm based on
entropy minimization.  ENT is capable of phasing both unrelated and
related genotypes coming from complex pedigrees.  The open source code
implementation of ENT and a web interface are publicly available at
http://dna.engr.uconn.edu/~software/ent/.


Building ENT 
------------

To build the ent executable run the following commands in the directory 
where ent_1.0.2.tar.gz is located:

tar -xzvf ent_1.0.2.tar.gz
cd ent_1.0.2
make all

After compiling the executable, make runs a regression test (assuming that
awk is installed on your system) to verify that they work as expected.  
Upon completion of this test you should get the message "REGRESSION TEST
SUCCESSFUL!!!"

The code has been compiled and tested successfully on Gentoo
Linux with gcc compiler versions 3.3.5 and 3.4.5.


Using ENT
---------

ENT is a command-line tool which by default reads from a user specified
input file and prints the results to the standard output.

Usage:
        ent [options] -input <input file> 
OPTIONS:
        -free N      : free window size (default: automatically selected)
        -locked N    : locked window size (default: automatically selected)
        -seed N      : random generator seed (default: 1)
        -no_batching : turn off batching (default: use batching)
        -count_all   : compute entropy over all haplotypes (default: count founder haplotypes only)


A sample input file (Hapmap release 23a genotypes for chromosome 22 
of CEU samples) is included with the code distribution.  Sample 
command:

ent -input chr22_CEU_r23a.ent_gen > chr22_CEU_r23a.ent_gen.ent_hap


File Formats
------------

ENT accepts sequences of the form 0/1/2/? where 0/1 denote the 
genotypes that are homozygous for the major/minor allele, 
2 denotes a heterozygous genotype, and ? denotes an unknown genotype.

The ENT input file format is as follows:

* First line: <number of individuals> <number of snps>

* Additional lines:

<individual id> <sex> <parent 1 id> <parent 2 id> <genotype sequence>

All individual id's must be non-zero, a parent id of 0 represents no 
known parent.

The output file format is as follows:

* First line: <number of individuals> <number of snps>

* Additional lines:

<individual id> <sex> <parent 1 id> <parent 2 id>
<haplotype 1 sequence>
<haplotype 2 sequence>

----------------
Revision history
----------------

Version 1.0.2 (10/5/08) - corrected a bug in the handling of large pedigrees 

Version 1.0.1 (8/23/08) - added upperbounds of 5 for free and locked in 
                          automatic window size selection to ensure more 
                          predictable runtime on large datasets

Contact Information
-------------------

For questions and bug reports please send e-mail to gusev@cs.columbia.edu
or bogdan@icsi.berkeley.edu or ion@engr.uconn.edu.

