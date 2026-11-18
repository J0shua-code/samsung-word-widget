# Word Face

Word Face is an Android app that shows Merriam-Webster’s Word of the Day on Samsung One UI lock screens. It provides two- and three-block layouts, renders the word in bold Instrument Serif and the definition in Instrument Serif, and schedules feed refreshes for 8:00am and 8:00pm local device time.

## Build

Requirements: JDK 17 and Android SDK Platform 35.

```sh
./gradlew assembleDebug
```

On Windows, run `gradlew.bat assembleDebug`.

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`. Install it, open the lock-screen editor, choose **Widgets**, and add the Word Face 2x1 or 3x1 widget. When updating an existing install, remove and re-add the widget to refresh its layout.

### Release signing

Release signing uses the local `app/keystore.properties` file. Copy `app/keystore.properties.example`, fill in the values, and place the referenced keystore beside it. Keep both files private and back up the keystore and its passwords securely; future app updates must use the same signing key. If the properties file is absent, Gradle creates an unsigned release APK.

## How it works

- Reads the public [Merriam-Webster Word of the Day RSS feed](https://www.merriam-webster.com/wotd/feed/rss2).
- Stores the latest word locally and refreshes the feed at 8:00am and 8:00pm local device time.
- Uses Samsung lock-screen `RemoteViews` provider metadata and a ServiceBox compatibility receiver.
- Renders text into bitmaps in the app process so One UI displays the bundled font consistently.

Android may defer alarms during power-saving modes, so refreshes can arrive shortly after the scheduled times. Samsung’s lock-screen widget metadata is undocumented and may behave differently across One UI releases and device models.

## Attribution

- **Instrument Serif** — Instrument Serif Project Authors. Distributed under the [SIL Open Font License 1.1](https://github.com/Instrument/instrument-serif); the license text is in `app/src/main/assets/OFL-Instrument-Serif.txt`.
- **Samsung One UI integration** — follows the [OneUI Project hidden API reference](https://oneuiproject.github.io/hidden-api/facewidget/) and the [Blur Widget Demo lock-screen guide](https://github.com/thatjoshguy67/blur-widget-demo/wiki/Lock-Screen-Widgets). Word Face is independent and is not affiliated with Samsung.
- **Merriam-Webster** — feed content and trademarks belong to Merriam-Webster. Word Face is not affiliated with or endorsed by Merriam-Webster.

## License

Word Face source code is licensed under the MIT License; see [`LICENSE`](LICENSE). Third-party fonts and feed content are excluded and remain under their respective terms.
