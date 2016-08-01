# commons-datamgmt
A Java API for reading and writing some common genetics and genomics data file formats, 
including [fastPHASE](http://stephenslab.uchicago.edu/software.html#fastphase), 
[Haploview](http://www.broadinstitute.org/scientific-community/science/programs/medical-and-population-genetics/haploview/haploview),
[MACH](http://csg.sph.umich.edu//abecasis/MaCH/), 
[PLINK](http://pngu.mgh.harvard.edu/~purcell/plink/), and more.

The `edu.uab.ssg.io.*` packages contain *parsers* and *writers* for various file formats, one format per package. 
Classes in `edu.uab.ssg.reports` demonstrate a few different ways you can implement some commonly-requested reports.
And the SNP marker, genotype, and sample data model is located in the `edu.uab.ssg.model.snp` package.

See [javadocs](https://jelaiw.github.io/commons-datamgmt/javadoc/) for further documentation.
