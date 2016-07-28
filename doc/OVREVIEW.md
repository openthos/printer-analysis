# Intro

This project belongs to [Openthos](https://github.com/openthos/openthos/wiki) project, providing local printers' and net printers' support.

# Feature

* Support usb printers.
* Support net pritners (http ipp smb lpd beh).
* Access to the android system print service and provide print function.
* Support print jobs' management(Hold, release/resume).
* Support printer's setting(within the drivers support).

# Supported printers

To see the details, Please visit:[SUPPORTED_PRINTERS.md](https://github.com/openthos/printer-analysis/blob/master/doc/SUPPORTED_PRINTERS.md)

# Known issues

| Intro | Category | remarks
|---|---|---|---|
|Duplex printing unsupported|print||
|Lack of the out of paper promote|print|CUPS unsupport this feature but some extra drivers may support|

# How to develop

Please follow the next steps.

## Building && Installing

Please visit:[BUILDING.md](https://github.com/openthos/printer-analysis/blob/master/doc/BUILDING.md)

## Making a CUPS component

Please visit:[MAKING_A_CUPS_COMPONENT.md](https://github.com/openthos/printer-analysis/blob/master/doc/MAKING_A_CUPS_COMPONENT.md)

## Coding a print app

Please visit:[APP.md](https://github.com/openthos/printer-analysis/blob/master/doc/APP.md)
