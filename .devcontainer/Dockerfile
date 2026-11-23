FROM mcr.microsoft.com/devcontainers/java:1-17-bookworm
RUN rm -f /etc/apt/sources.list.d/yarn.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends libxxf86vm1 libgl1 libgtk-3-0 libxtst6 \
 && rm -rf /var/lib/apt/lists/*
 && apt-get install -y --no-install-recommends libxxf86vm1 libgl1 libgtk-3-0 libxtst6 git-lfs \