---           
layout: post
title: "Apache Tuscany : Part 1 - Installing Tuscany Plug in in eclipse"
date: 2011-07-06 16:13:08 UTC
updated: 2011-07-06 16:13:08 UTC
comments: false
categories: apache tuscany eclipse
---

Apache Tuscany is an open source implementation of Service Component Architecture (SCA).This series of posts discuss how to use tuscany in your projects. This tutorial explains  how to install and configure tuscany in eclipse(3.4 and above).

###Step 1 : 
Launch Install New Software (3.5) or Find new software (3.4) from Help.
In Eclipse 3.5  

> Help-->Install New Software 

In Eclipse 3.4:    

> Help-->Software Updates -->Available Software


###Step 2: 
 Add the following site 

 [http://www.apache.org/dist/tuscany/
 java/sca/
 1.5.1/tuscany-sca-1.5.1-updatesite/](http://www.apache.org/dist/tuscany/java/sca/1.5.1/tuscany-sca-1.5.1-updatesite/)

Give some time  till eclipse will fetch necessary dependencies

###Step 3 : 
Once fetching is complete , select Apache tuscany core feature

###Step 4:   
Eclipse requires old update manager to install the plug in 

![](http://4.bp.blogspot.com/-hyRfqb65Jow/ThSI2JLtCBI/AAAAAAAAAVY/Sv82BL2biik/s640/pic1.png)

Allow eclipse to use old update manager . Click on launch

###Step 5:   

 Now you got old update manager . Select “Search for new feature to install” and press next.

###Step 6:   

Click on New Remote Site and enter the above url in the URL field and give name as tuscany

![](http://2.bp.blogspot.com/-yQ5eQHljwG8/ThSJQ25XZUI/AAAAAAAAAVc/2nbyDydhLNc/s640/pic2.png)


###Step 7:

Press OK and Update manager searches in the url and gives the below screen

![](http://4.bp.blogspot.com/-dsFGWwM9d_8/ThSJcOAQNdI/AAAAAAAAAVg/XAQcjyeXvSY/s640/pic3.png)


Select tuscany and press Next . Accept the licence and hit Next and then finish. There will be a window showing some error . Don't bother about it.Just close that window. In progress view , you can see the progress of installation.In slow connection , eclipse shows some exception in getting jar and asks for retry. Just click yes , whenever exception arises. 

###Conclusion:

Once download is complete , it will ask for installing the download . Just select “Install all”. Once the installation is complete , just restart the eclipse . You are set to go with the Apache tuscany.

