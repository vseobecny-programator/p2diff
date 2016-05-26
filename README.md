# P2 Diff Tool

A tool for generating a diff overview of 2 update sites

# Usage

1.) ... http://url.to.original.file.zip http://url.to.original.file2.zip . http://url.to.revised.file.zip ...
    --filter=<regex>
    --extract1="path to extracted original zip"
    --extract2="path extracted revised zip"
    --target="path to generated diffs"

2.) ... -f <filename.properties>

    Syntax:
    -------
    original=zip1, zip2, zip3
    revised=zip4, zip5
    filter=<regex>
    extract1="path to extracted original zip"
    extract2="path extracted revised zip"
    target="path to generated diffs"
