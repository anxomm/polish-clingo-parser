{rain;a;p1;b;p2;tv;workday;c;d;e;weekend;happy;h;r;w;t1;t2}.

% > | rain - weekend - happy .
:- not weekend, happy.
:- rain, happy.

% = weekend - workday .
:- not weekend, not workday.
:- weekend, workday.

% % weekend workday .
:- not weekend, not workday.
:- weekend, workday.

% - % a b .
:- b, not a.
:- b, not b.
:- a, not a.
:- a, not b.

% | | a - b | - c & d - e .
:- not a, b, c, e.
:- not a, b, c, not d.

% = p1 & - t1 t2 .
:- p1, t1.
:- not p1, not t1, t2.
:- p1, not t2.

% = p2 % t1 t2 .
:- not p2, t1, not t2.
:- not p2, t1, not t1.
:- p2, not t1, not t2.
:- p2, t1, t2.
:- not p2, t2, not t1.
:- not p2, t2, not t2.

% % p1 p2 .
:- not p1, not p2.
:- p1, p2.

% > w - tv .
:- w, tv.

% > & r - w h .
:- r, not w, not h.

% & tv - h .
:- not tv.
:- h.

