grammar ExpandedSample;

start
    : leftRef
    | rightRef
    ;

leftRef
    : TOKEN
    ;

rightRef
    : TOKEN OTHER
    ;

OTHER
    : 'x'
    ;
