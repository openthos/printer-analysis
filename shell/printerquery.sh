#!/bin/bash
# Author: Tao<taocr2005@163.com>
# Created: 2016-07-12
# Description:
# Used to inquire the property of the printer you input.
lpoptions -p $1 && lpoptions -p $1 -l
