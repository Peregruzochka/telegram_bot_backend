$path_to_service_name_dir = "C:\Users\Peregruzochka\Documents\GitHub\telegram_bot"
$service_name = "users"

git -C $path_to_service_name_dir pull origin master
docker-compose build $service_name
docker-compose up -d --no-deps $service_name
docker image prune -f