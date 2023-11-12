# hairball
The Hairball meta-language.

This project is a split from the larger catfood project in order to support Hairball as a stand-alone project.
In order to support compiling the hairball_core portion with J2CL for use in client-side web applications and
as a component of mobile apps, etc. This project is being converted to use Bazel as a build system instead of
Gradle. J2CL is simply not supported by any other build tool and it is the only really viable option for building
a usable web/javascript version of Hairball. 

Note that nothing here actually works yet, this is simply a very preliminary initial set of commits.
