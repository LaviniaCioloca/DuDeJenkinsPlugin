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
