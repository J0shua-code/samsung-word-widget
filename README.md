Word Face

Word Face puts Merriam-Webster's Word of the Day on your Samsung lock screen.

Samsung doesn't offer an official way for third-party apps to add widgets to the One UI lock screen. Word Face gets around this by using Samsung's hidden, undocumented lock-screen widget APIs, so a small 2x1 widget can sit right on the lock screen without any root access or system modifications.

Features
Lock-screen widget: a 2x1 widget that shows the current word right on the lock screen.
Always fresh: the word is refreshed twice a day, at 8:00am and 8:00pm local time.
Consistent typography: text is drawn in the bundled Instrument Serif font so it looks the same on every device.
Lightweight: it reads one public feed and stores the latest word locally.
Adding the widget
Install the app.
Open the lock-screen editor and choose Widgets.
Add the Word Face 2x1 widget.

If you're updating an existing install, remove and re-add the widget to refresh its layout.

How it works
Hidden Samsung APIs: Word Face exposes Samsung's lock-screen RemoteViews provider metadata, along with a ServiceBox compatibility receiver, which is what makes One UI treat the widget as a lock-screen widget.
Data source: it reads the public Merriam-Webster Word of the Day RSS feed and stores the latest word on the device.
Scheduling: alarms trigger a feed refresh at 8:00am and 8:00pm local time.
Rendering: text is rendered into bitmaps inside the app process, because One UI's lock screen can't load custom fonts for widgets directly.
Limitations
Samsung's lock-screen widget metadata is undocumented. It may behave differently, or stop working, across One UI versions and device models.
Android may defer alarms in power-saving modes, so a refresh can arrive a little after the scheduled time.
Attribution
Instrument Serif: Instrument Serif Project Authors, distributed under the SIL Open Font License 1.1. The license text is in app/src/main/assets/OFL-Instrument-Serif.txt.
Samsung One UI integration: based on the OneUI Project hidden API reference and the Blur Widget Demo lock-screen guide. Word Face is independent and is not affiliated with Samsung.
Merriam-Webster: feed content and trademarks belong to Merriam-Webster. Word Face is not affiliated with or endorsed by Merriam-Webster.
License

Word Face source code is licensed under the MIT License; see LICENSE. Third-party fonts and feed content are excluded and remain under their respective terms.

## GitHub Actions releases

The Release APK workflow builds and publishes a signed GitHub release whenever a `v*` tag is pushed. Before the first release, add these repository secrets under **Settings → Secrets and variables → Actions**:

- `ANDROID_KEYSTORE_BASE64` — single-line Base64 encoding of `app/release-key.p12`. In PowerShell, generate it with `[Convert]::ToBase64String([IO.File]::ReadAllBytes("app/release-key.p12"))`.
- `ANDROID_KEYSTORE_PASSWORD` — the keystore password from your local `app/keystore.properties`.
- `ANDROID_KEY_ALIAS` — the key alias from your local `app/keystore.properties`.
- `ANDROID_KEY_PASSWORD` — the key password from your local `app/keystore.properties`.

After adding the secrets, push a version tag such as `v1.0` to build the APK and attach it to the GitHub release. Keep the keystore and passwords out of Git; Base64 is only an encoding, so store the encoded value as a GitHub Actions secret.
