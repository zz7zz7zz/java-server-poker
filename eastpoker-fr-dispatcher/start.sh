nohup java -jar Dispatcher.jar -n 'Dispatcher' -i 0 -p 10000 &
sleep 3

nohup java -jar Dispatcher.jar -n 'Dispatcher' -i 1 -p 10001 &
sleep 3
