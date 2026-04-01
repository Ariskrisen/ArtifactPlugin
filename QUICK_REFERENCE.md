# Артефакты - Быстрая шпаргалка

## Создание своего артефакта

### 1. Простой артефакт (эффект зелья)

```yaml
artifacts:
  my_charm:
    material: SUGAR
    name: "§bТалисман Скорости"
    lore:
      - "§7Быстрый как ветер"
    enabled: true
    effects:
      potion-effects:
        - effect: SPEED
          level: 2
```

### 2. Артефакт с несколькими эффектами

```yaml
artifacts:
  super_artifact:
    material: NETHER_STAR
    name: "§6Супер Артефакт"
    lore:
      - "§7Мощный артефакт"
    enabled: true
    effects:
      potion-effects:
        - effect: SPEED
          level: 2
        - effect: JUMP_BOOST
          level: 1
      attack-effects:
        - type: LIFESTEAL
          percent: 10
          chance: 1.0
      defense-effects:
        - type: ABSORB
          percent: 20
          chance: 1.0
```

### 3. С ItemsAdder

```yaml
artifacts:
  custom_item:
    material: "itemsadder:my_custom_texture"
    name: "§eКастомный Артефакт"
    lore:
      - "§7Текстура из ItemsAdder"
    enabled: true
    effects:
      potion-effects:
        - effect: STRENGTH
          level: 1
```

### 4. С CustomModelData

```yaml
artifacts:
  textured:
    material: GLOW_BERRIES
    custom-model-data: 1001
    name: "§eАртефакт с Текстурой"
    lore:
      - "§7Имеет кастомную текстуру"
    enabled: true
    effects:
      potion-effects:
        - effect: SPEED
          level: 1
```

## Все эффекты зелий

| Код | Эффект |
|-----|--------|
| `SPEED` | Скорость |
| `SLOWNESS` | Замедление |
| `HASTE` | Спешка |
| `STRENGTH` | Сила |
| `JUMP_BOOST` | Прыгучесть |
| `REGENERATION` | Регенерация |
| `RESISTANCE` | Сопротивление |
| `FIRE_RESISTANCE` | Огнестойкость |
| `WATER_BREATHING` | Подводное дыхание |
| `INVISIBILITY` | Невидимость |
| `NIGHT_VISION` | Ночное зрение |
| `ABSORPTION` | Поглощение |
| `HEALTH_BOOST` | Увеличение здоровья |
| `SATURATION` | Насыщение |
| `LUCK` | Удача |
| `SLOW_FALLING` | Медленное падение |
| `LEVITATION` | Левитация |
| `GLOWING` | Свечение |
| `CONDUIT_POWER` | Сила проводника |
| `DOLPHINS_GRACE` | Грация дельфина |

## Эффекты атаки

| Тип | Описание | Параметры |
|-----|---------|-----------|
| `LIFESTEAL` | Вампиризм | `percent: 10` |
| `IGNITE` | Поджог | `duration: 60` |
| `POISON` | Яд | `damage: 2, duration: 80` |
| `WITHER` | Иссушение | `duration: 80` |
| `CRITICAL` | Критический удар | `chance-bonus: 0.15, damage-multiplier: 1.5` |
| `LIGHTNING` | Молния | `chance: 0.15` |
| `KNOCKBACK` | Отброс | `strength: 2.0` |
| `SLOW` | Замедление | `level: 2, duration: 60` |

## Эффекты защиты

| Тип | Описание | Параметры |
|-----|---------|-----------|
| `ABSORB` | Поглощение урона | `percent: 20` |
| `DODGE` | Уклонение | `chance: 0.15` |
| `THORNS` | Шипы | `damage: 2` |
| `REFLECT` | Отражение | `percent: 25` |

## Пассивные способности

| Тип | Описание | Параметры |
|-----|---------|-----------|
| `MAGNETIC` | Притягивание предметов | `range: 5` |
| `DOUBLE_JUMP` | Двойной прыжок | `boost: 1.5, cooldown: 100` |
| `SOUL_SPEED` | Скорость душ | `level: 2` |
| `FROST_WALKER` | Ледоход | `level: 1` |
| `HERO_OF_THE_VILLAGE` | Герой деревни | - |
