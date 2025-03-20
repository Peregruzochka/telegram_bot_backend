$path_to_service_name_dir = "C:\Users\Peregruzochka\Documents\GitHub\tg_bot_admin"
$path_to_docker_compose_dir = "C:\Users\Peregruzochka\Documents\GitHub\telegram_bot_backend"
$service_name = "admin"
$compose_file = "docker-compose.prod.yaml"

git -C $path_to_service_name_dir pull origin master

cd $path_to_docker_compose_dir
docker-compose -f "$compose_file" build $service_name
docker-compose -f "$compose_file" up -d --no-deps $service_name
docker image prune -f
