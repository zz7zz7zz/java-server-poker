nohup java -jar Dispatcher.jar -n 'Dispatcher' -i 0 -p 10000 > dispatcher_0.out &
sleep 3

nohup java -jar Dispatcher.jar -n 'Dispatcher' -i 1 -p 10001 > dispatcher_1.out &
sleep 3
