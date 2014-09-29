---           
layout: post
title: "Boot custom recovery without flashing"
date : 2014-09-26
categories: android androidone
---
In Android custom recoveries like [CWM](https://clockworkmod.com/rommanager) allow users to access advanced functionalities like rooting,backup,installing kernels etc. But by flashing custom recovery user will loose the stock recovery and ability to get OTA from manufacture or Google.

So if you want to use a custom recovery,just to root or take a backup you can do that without flashing it permanently. Use the following command to boot custom recovery

	fastboot -c "lge.kcal=0|0|0|x" boot <path to recover.img>

So now you don't need to give up OTA to get custom recoveries.