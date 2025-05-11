$path_to_service_name_dir = "C:\Users\Peregruzochka\Documents\GitHub\tg_bot_admin"
$path_to_docker_compose_dir = "C:\Users\Peregruzochka\Documents\GitHub\telegram_bot_backend"
$service_name = "admin-test"
$compose_file = "docker-compose.test.yaml"
$project_name = "test-space"

git -C $path_to_service_name_dir pull origin dev

cd $path_to_docker_compose_dir
docker compose -f "$compose_file" --project-name $project_name build $service_name
docker compose -f "$compose_file" --project-name $project_name stop $service_name
docker compose -f "$compose_file" --project-name $project_name rm -f $service_name
docker compose -f "$compose_file" --project-name $project_name up -d --no-deps $service_name
docker image prune -f
