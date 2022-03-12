# Backup
- Config automatically saved when server stopped
- Directory: `.../plugins/MiniGameWorld_backup`
- Format: `2021-10-03+19;07;58` means saved at `2021year 10month 3day PM 7hour 7minute 58second`

# How to apply backup data
1. Find the folder you want to backup in `plugins/MiniGameWorld_backup`
2. Go inside the folder and copy the all contents (`minigames` folder and `settomgs.yml` file)
3. Paste and override them in the `plugins/MiniGameWorld` folder
4. If your server is running, run `/mw reload` command to apply the copied backup data (OP required)