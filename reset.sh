#!/bin/bash
echo "=== Nettoyage des conteneurs Docker ==="
sudo docker rm -f $(sudo docker ps -aq) 2>/dev/null || true
sudo docker network prune -f

echo "=== Suppression forcée des conteneurs actifs ==="
for id in $(sudo docker ps -aq); do
  pid=$(sudo docker inspect --format '{{.State.Pid}}' $id 2>/dev/null)
  if [ -n "$pid" ] && [ "$pid" -ne 0 ]; then
    echo "Conteneur $id encore actif → kill PID $pid"
    sudo kill -9 $pid
    sudo docker rm -f $id
  fi
done

echo "=== Libération automatique des ports exposés par Docker Compose ==="
ports=$(sudo docker compose config | grep "published:" | awk '{print $2}' | sort -u)

for port in $ports; do
  # Cherche processus classiques
  pids=$(sudo lsof -t -i TCP:$port 2>/dev/null)
  # Cherche aussi docker-proxy
  dpids=$(ps -ef | grep "docker-proxy.*:$port" | grep -v grep | awk '{print $2}')
  all_pids="$pids $dpids"

  if [ -n "$all_pids" ]; then
    echo "Port $port occupé par PID(s): $all_pids → kill"
    sudo kill -9 $all_pids
  else
    echo "Port $port libre"
  fi
done

echo "=== Relance du stack Docker ==="
sudo docker compose up -d

