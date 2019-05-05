# toggl-timesheet

A Clojure library designed to extract your monthly timesheet for a given Toggl workspace.

## Usage

Customise values in `user-env`

Launch a repl with `lein repl`

```
user=> (use '[toggl-timesheet.core :as timesheet])
nil
user=> (print (timesheet/print-report "2019-04-01" "2019-04-30"))
Emilien clocked a total of 12.34 hours in the Automation workspace from 2019-04-01 to 2019-04-30.
This amounts to 740.40€ at the rate of 60€ per hour.

|-------+--------------------------------------------------------------------|
| Hours | Task                                                               |
|-------+--------------------------------------------------------------------|
| 11.73 | Write code to automate the generation of monthly report from Toggl |
|  0.47 | Copy paste data manually from Toggl                                |
|  0.09 | Edit and send invoice                                              |
|  0.05 | Meditate on automation, practice and efficiency                    |
|-------+--------------------------------------------------------------------|
nil
```
## Meditation

[![XKCD - Is it worth the time?](https://imgs.xkcd.com/comics/is_it_worth_the_time.png)](https://xkcd.com/1205/)


[![XKCD - Automation](https://imgs.xkcd.com/comics/automation.png)](https://xkcd.com/1319/)

## License

Copyright © 2019 Emilien Ah-Kiem

Distributed under the WTFPL version 2
