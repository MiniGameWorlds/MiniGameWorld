# Backup
## With command
- `/mw backup [<backup-folder>]`
- If `[<backup-folder>]` argument is not given, backup folder name will be current date (e.g. `2022-05-07T19;59;46`)
- Backup will be processed after current server data saved

## On server stops
- Config automatically saved when server stopped
- Directory: `.../plugins/MiniGameWorld_backup`
- Format: `2021-10-03+19;07;58` means saved at `2021year 10month 3day PM 7hour 7minute 58second`

# How to load backup data
## With command
- If your server is running, run `/mw reload [<backup-folder>]` command to apply the backup data (OP required)

## With file copy
- But also you can do with folder copy
1. Find the folder you want to backup in `plugins/MiniGameWorld_backup`
2. Go inside the folder and copy the all files and folders
3. Paste and override them in the `plugins/MiniGameWorld` folder
4. run `/mw reload` command to apply the copied backup data (OP required)