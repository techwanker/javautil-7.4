Trace Record Fields
===================

code-block::

                                           Cursor Operation 
               Type     Parsing ParseError Parse EXec Fetch Stat  Lobread Lobpgsize Close
    &#35;  cursorNumbe             X                X    X   X          
    ad     sgaAddress      X
    bytes  bytes                                                     X      X
    c      CpuMicroSec                        X     X    X           X      X         X
    card   cardinality
    cnt                                                      O
    cost   cost (optim
    cr     consistentR                        X     X    X           X      X
    cu     currentMode                        X     X    X           X      X
    dep    depth           X       X          X     X    X                            X
    e      elapsedMicr                        X     X    X           X      X         X
    err    oracleError             X
    hv     sqlHashValu     X
    id                                                       X
    len    sqlTeXtLeng     X       X
    lid                    X       X
    mis    libraryCach                        X     X    X
    obj    objectNumbe                                       X
    oct    oracleComma     X       X
    og     optimizerGo                        X     X    X
    op     operation                                           X
    p      physicalBlo                        X     X    X              X       X
    pid    processId                                           X
    plh                                       X     X    X
    pos    position (o                                         X
    pw     physicalWri                                         X
    r      rowCount                           X     X    X
    size                                                       O
    sqlid  sqlId           X
    str                                                                 X
    tim                                       X     X    X              X       X        X
    time
    type                                                                                                          X
    uid                    X      X

