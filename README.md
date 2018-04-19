# Integrating Azure AD into a Java web application

## Quick Start

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2Fjava-webapp-oidc-migrate-poc%2Fmaster%2Fazuredeploy.json" target="_blank"><img src="http://azuredeploy.net/deploybutton.png"/></a>

__Details__

This Java app will give you with a quick and easy way to set up a Web application in Java using OAuth2. The sample included in the download is designed to run on any platform and tested with Java 8. This repo is based on the original repo at https://github.com/Azure-Samples/active-directory-java-webapp-openidconnect but has been updated with the following features:
* Uses Bootstrap to give it a nice layout similar to Visual Studio .Net web project templates
* Demonstrates a pattern for combining Azure AD authentication side by side with "legacy auth". The ARM template will create a simple Azure SQL database with a "User" table that has an email and a password column. The pattern allows a user to log in using "credentials" from the legacy process, or login to Azure AD and then automatically "link" the Azure AD account to the legacy account.
* Unlike most Java ARM templates, this one actually deploys the code for you as well. There is a custom "deploy.cmd" file that uses the Java tools present in Azure Web Apps to compile the Java classes and create the WAR file for automated deployment. If you fork this project, you can directly connect your app and enjoy continuous integration from GitHub! https://blog.github.com/2015-09-15-automating-code-deployment-with-github-and-azure/
	
IMPORTANT: the "legacy auth system" articulated here is in NO WAY meant to model a best practice for legacy authentication. The database table, hosted in an Azure SQL database, includes a CLEAR TEXT password. Surprisingly, there are still systems running today using such a process but it is unwise in the extreme and should be considered an anti-pattern.

* ARM template deploys the following:
  * Azure Web App with this source code
  * Azure SQL DB
* Requires the following (see step-by-step deployment instructions above for details):
  1. Azure AD application with the following:
      * Sign in and read user profile
  
### Step 1: Register an Azure AD Tenant

To use this sample you will need a Azure Active Directory Tenant. If you're not sure what a tenant is or how you would get one, read [What is an Azure AD tenant](http://technet.microsoft.com/library/jj573650.aspx)? or [Sign up for Azure as an organization](http://azure.microsoft.com/documentation/articles/sign-up-organization/). These docs should get you started on your way to using Azure AD.

### Step 2: Setup

An Azure Active Directory app must be created in your tenant:

* Log into the Azure portal, and click on Azure Active Directory, then click on Properties
* Click to copy the "Directory ID". This is also referred to as a "Tenant Id". Save this string, you'll need it in a bit.
* Click on App registrations
* Click "+ New application registration" and enter the name of your app (like "My Java Sample"). This title will be seen when users are prompted for their credentials.
* Select "Web app / API", and enter the Sign-on URL. If you're setting this up before you deploy the app to Azure, you can enter http://loopback as a placeholder. Click "Create". The default permissions will work fine.
* Back on the Settings panel, click "Keys". Under Description, enter a name for the application key, like "Key 1". Select an expiration date for the key.
* Click "Save". An application secret will be generated and displayed. COPY this key and record it - you'll need it in a minute when setting up the web application. NOTE: this key will not be displayed again and cannot be retrieved. If you lose it, you'll have to come back, delete it, and create another one.
* Click "Properties", and click "Yes" under Multi-tenanted at the bottom of the Properties panel then click "Save". This enables your application to authenticate users from other Azure AD tenants. NOTE: The App ID URI must match your Azure AD domain name if you choose the multi-tenant option.
* Finally, before we are done with the first app, record the "Application ID". You can click to the right of it in the main panel and it will copy it to your clipboard. Record it along with the app secret from above - these two strings will be needed to setup the web app.
* Use the info copied above to complete filling out the ARM template form (or the azuredeploy.parameters.json file if you choose to deploy via automation).
* Once deployed, return to the Azure AD app and update the URL and the Reply URL. Set the Reply url to your new app URL, like "https://mynewapp.azurewebsites.net/" (note the trailing slash).
* Log in!

### Optional Azure AD App Setup
If you'd rather use a command line to create your Azure AD app, try PowerShell:
```powershell
$appName = "[Your Azure AD app name]"

if ($ctx -eq $null) { $ctx = Connect-AzureRmAccount }
$md5     = new-object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
$hash    = $md5.ComputeHash(([System.Guid]::NewGuid()).ToByteArray()) + $md5.ComputeHash(([System.Guid]::NewGuid()).ToByteArray())
$appPw   = [System.Convert]::ToBase64String($hash)
$secPw   = ConvertTo-SecureString $appPw -AsPlainText -Force -ErrorAction Stop
$app     = New-AzureRmADApplication `
            -DisplayName $appName `
            -HomePage "https://$($appName.Replace(' ', ''))/" `
            -IdentifierUris "https://testdomain.local/$($appName.Replace(' ', ''))" `
            -Password $secPw `
            -AvailableToOtherTenants $true

$sp = New-AzureRmADServicePrincipal -ApplicationId $app.ApplicationId

Write-Output "--------"
Write-Output "AppID: $($app.ApplicationId)"
Write-Output "AppSecret: $($appPw)"
Write-Output "Tenant Name: $($ctx.Context.Tenant.Directory)"
Write-Output "TenantId: $($ctx.Context.Tenant.Id)"
```

Or use the Azure command line (CL):
```bash
az ad app create --display-name "[Your Azure AD app name]" --homepage "[Your home page URL]" --password "[Really long password that ideally should be generated as above]" --identifier-uris [Your custom app URI] --available-to-other-tenants true

az ad sp create --id [AppID returned from previous command output]
```

### Acknowledgements

We would like to acknowledge the folks who own/contribute to the following projects for their support of Azure Active Directory and their libraries that were used to build this sample. In places where we forked these libraries to add additional functionality, we ensured that the chain of forking remains intact so you can navigate back to the original package. Working with such great partners in the open source community clearly illustrates what open collaboration can accomplish. Thank you!


# Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.microsoft.com.

When you submit a pull request, a CLA-bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., label, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
