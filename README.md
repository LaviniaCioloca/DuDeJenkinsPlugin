# DuDeJenkinsPlugin

## About
Jenkins plugin that gives DuDe duplication analysis in an HTML report for each build.

## Jenkins integration
To have DuDe analyse the duplications in your repository on each build you need to login as an **administrator** and:

* Add the custom plugin in Jenkins
  * Download the latest DuDeJenkinsPlugin.hpi file from the [Release section](https://github.com/LaviniaCioloca/DuDeJenkinsPlugin/releases)
  * Go to **Manage Jenkins** → **Manage Plugins** → **Advanced**
  * In the **Upload Plugin** section add the .hpi file and click on **Upload**
  * After the plugin is installed select the checkbox to `Restart Jenkins when installation is complete and no jobs are running`

After Jenkins restarts you have to:

* Enable CSS in Jenkins which is disabled by default due to Content Security Policy
  * Go to **Manage Jenkins** → **System Configuration** → **Manage Nodes and Clouds**
  * Click on the _master_ node and in the right side go to **Script Console** and paste the following line
  ```java
  System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")
  ```
  
Optional step: If you want the build to fail based on the results of DuDeJenkinsPlugin:
* Install from **Plugin Manager** the [Post build task plugin] (https://plugins.jenkins.io/postbuild-task/) which allows to specify in a shell script the criteria for build's success/fail in Jenkins **Post-build Actions** section

* As a **Post build task** for this plugin select the following configuration:
  * Log text: `DuDe analysis finished!`
  * Operation: `AND`
  * Script:
  
  ```sh
  export maximum_duplication_percentage_in_project=20.0

  export maximum_duplication_percentage_increase_allowed=5.0

  sh DuDe-analysis.sh
  ```
  
  * And check the checkbox for `Escalate script execution status to job status`
