#!/bin/bash

#启动监视器
cd Monitor
./start.sh
cd ../

#启动Dispatcher
cd Dispatcher
./start.sh
cd ../

#启动Access
cd Access
./start.sh
cd ../

