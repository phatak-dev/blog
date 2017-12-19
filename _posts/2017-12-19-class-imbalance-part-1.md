---
layout: post
title: "Class Imbalance in Credit Card Fraud Detection - Part 1 : Understanding Effect on Model Accuracy"
date : 2017-12-19
categories: scala spark datascience class-imbalance python
---
Whenever we do classification in ML, we often assume that target label is evenly distributed in our dataset. This helps the training algorithm to learn the features as we have enough examples for all the different cases. For example, in learning a spam filter, we should have good amount of data which corresponds to emails which are spam and non spam.

This even distribution is not always possible. Let's take an example of fraud detection. Fraud detection is a use case, where by looking at transaction we need to decide is the transaction is fraudulent or not. In majority of the cases, the transaction will be normal. So the data for fraudulent data is very small compared to normal ones. In these cases, there will be imbalance in target labels. This will effect the quality of models we can build.So in next series of posts we will discuss about what's class imbalance and how to handle it in python and spark. 

This is the first post in the series where we discuss about class imbalance and it's effect on classification model accuracy. You can read all the blogs in the series [here](/categories/class-imbalance).

## Data

For our example, we will use credit card fraud data. This data has more than 30 variable about transaction and target column **Class** which signifies given transaction is fraud or not. You can learn more about data in [kaggle](https://www.kaggle.com/dalpozz/creditcardfraud).

## Class Imbalance

Let's plot distribution of the target label using seaborn.

{% highlight python %}
sns.countplot(x='Class', data=df)
{% endhighlight %}

The distribution looks as below

![Distribution image](/images/credit_card_class_plot.png)

As you can observe from the plot, we have so many 0 (non-fraud) compared to 1 (fraud). This kind of imbalance in the target variable is known as class imbalance.


## Classification on Imbalanced Data

Let's run the logistic regression to do the classification on this imbalanced data. 


### Data Preparation

Before running algorithm, we need normalise *Amount* column and then drop *Time* column. 

{% highlight python %}

from sklearn.preprocessing import StandardScaler
from sklearn.cross_validation import train_test_split

df['normal_amount'] = StandardScaler().fit_transform(df['Amount'].values.reshape(-1,1))
df = df.drop(['Amount','Time'], axis=1)
X = df.loc[:,df.columns != 'Class']
y = df.loc[:,df.columns == 'Class']
{% endhighlight %}


### Logistic Regression

Now we will run logistic regression

{% highlight python %}

X_train, X_test, y_train, y_test = train_test_split(X,y,test_size = 0.3, random_state = 0)

# Calculate the recall score for logistic Regression on Skewed data
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import recall_score,accuracy_score
lr = LogisticRegression()
lr.fit(X_train,y_train)
y_pred = lr.predict(X_test)
print(accuracy_score(y_test,y_pred))
{% endhighlight %}

The below is the output

{% highlight text %}

0.999204147794

{% endhighlight %}

From the above result, you may be happy that algorithm is performing extremely well . But it's not true. As most of the labels 0, even random
guess gives you 99% accuracy. So we need a better measure to understand the performance of the model.


### Recall 

Recall is a measure which measures the ability of model to predict right for a given label. In our case, we want to test the model how accurately it can recall fraud cases as we are interested in that. We can calculate it using *recall_score*.

{% highlight python %}

print(recall_score(y_test,y_pred,average=None)

{% endhighlight %}

In above code, we are calculating recall. *average* parameter makes sure that recall is returned individually for each label. The output will be

{% highlight text %}

[ 0.99985931  0.61904762]

{% endhighlight %}

As you can observe from the results, the recall for 1.0 is only 0.61904762 compared to 99% for 0. So our model is not doing a good job of recognising frauds. So this shows that how imbalanced data is effecting accuracy of model.

You can access complete code from python notebook from [github](https://github.com/phatak-dev/spark-ml-kaggle/blob/master/python/credit_card_class_imbalance.ipynb) or live notebook on [kaggle](https://www.kaggle.com/madhukaraphatak/under-sampling-to-achieve-better-recall).


## Conclusion

In this post we understood what is class imbalance and how it effects the accuracy of model.

## What's Next?

In our next post, we will discuss how to handle class imbalance in python.

