# Update checker
Update checker always checks version of MiniGameWorld plugin by tracking the [plugin github latest release](https://github.com/MiniGameWorlds/MiniGameWorld/releases) version.

### Normal
![need-no-update](https://user-images.githubusercontent.com/61288262/180635574-154559b7-b893-4675-a754-37321772d635.png)
### Need update
![need-update](https://user-images.githubusercontent.com/61288262/180635577-7f78b492-b2d1-4404-8836-ca8b1bb9ce6d.png)



# Warnings
If you load the plugin too often, the plugin will be stopped during loading. Because update check can not be used too much. (Related with Github API rate limits)

So if you want to turn off the update check, set `check-update` to **false** in your `MiniGameWorld/settings.yml` config.