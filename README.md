# ElytraBloker - плагин для отключения элитр в эндер-мире

## Команды

`/elyblock toggle` - вкл/выкл элитры

`/elyblock reload` - перезагрузить плагин

## Конфигурация

```yaml
# Сообщения
# Форматирование с помощью https://docs.advntr.dev/minimessage/format.html
reloadConfig: "<green>Конфигурация перезагружена!"
noPermission: "<red>Недостаточно прав!"
elytraUseMessage: "<red>Элитры отключены!"

# Отключены ли элитры
isEnable: true
# Выкидывать ли элитры когда игрок пытается взлететь
isDropElytra: true
```

## Права

`elytrablocker.admin` - использование плагина