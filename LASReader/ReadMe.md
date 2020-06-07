LAS Reader

This tool is created to read and create Wells from well log files (.las format).

Run with 1 input parameter - path (to file or folder).

If given input is path - reader will recursively loop through folder in order to find files with .las extension (case invensitive).
After files are located or if the input is a single file - content reading process begins.

Files are processed using instance of LASProcessor class, which reads file content and creates Well objects.
Each file is read line-by-line with current section checking. Main sections (Well information, Curve information, Parameter information) are parsed in the same way due to their identical formatting. 
Different types of output WellObject inheritors are created using and then are added to the corresponding well.

The result of the execution is a list of wells with parsed parameters.


