$path_to_service_name_dir = "C:\Users\Peregruzochka\Documents\GitHub\telegram_bot_backend"
$project_name = "telegram-bot-backend"
$docker_file = "docker-compose.test.yaml"

Set-Location -Path $path_to_service_name_dir

docker compose -f $docker_file --project-name $project_name up -d