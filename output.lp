{rain;workday;weekend;happy}.

% > | rain - weekend - happy .
:- not weekend, happy.
:- rain, happy.

% = weekend - workday .
:- not weekend, not workday.
:- weekend, workday.

% % weekend workday .
:- not weekend, not workday.
:- weekend, workday.

