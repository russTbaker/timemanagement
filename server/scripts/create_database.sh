#!/bin/bash
SCRIPT=${1}
mysql timemanagement -u root < ${SCRIPT}
