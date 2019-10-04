# Graylog-Output-Carrier plugin

Stream output plugin for Graylog. This plugin allows posting notifications to Slack and Telegram.

### Prerequisites

- Java 1.8.0 
- Maven 3.1.1

### Installation

1. Create plugin package:
    ```
    mvn package
    ```
2. Copy to:
    ```
    /usr/share/graylog-server/plugin/
    ```
3. Restart Graylog

### Configuration

Add output in Graylog > Streams > Manage Output > Select Output Type > Launch new output.

Settings:

* `Webhook type` - Select messenger type:
    * `Slack`
    * `Telegram`
* `Webhook URL` - provide full webhook URL:
    * `Slack` - https://hooks.slack.com/services/XXX
    * `Telegram` - https://api.telegram.org/botXXX/sendMessage
* `Messenger channel` - required only for Telegram
* `Level` - Set limit messages level (min 0, max: 7), values work like Syslog
* `Grace` - Wait (sec) between send, rest will be ignored (min: 1, max: 60)
* `Text limit` - Text message limit (min 100, max 3000)
* `Ignored facilities` - Ignored facilities separated by comma
* `Additional fields` - Additional fields separated by comma
* `Graylog URL` - URL to your Graylog web interface
