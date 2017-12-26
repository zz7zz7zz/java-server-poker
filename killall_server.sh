ps -ef | grep Monitor | grep -v grep | awk '{print $2}' | xargs kill -9

ps -ef | grep Dispatcher | grep -v grep | awk '{print $2}' | xargs kill -9

ps -ef | grep Access | grep -v grep | awk '{print $2}' | xargs kill -9

