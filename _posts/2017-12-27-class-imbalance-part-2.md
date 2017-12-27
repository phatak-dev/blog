---
layout: post
title: "Class Imbalance in Credit Card Fraud Detection - Part 2 : Undersampling in Python"
date : 2017-12-27
categories: scala spark datascience class-imbalance python
---

Whenever we do classification in ML, we often assume that target label is evenly distributed in our dataset. This helps the training algorithm to learn the features as we have enough examples for all the different cases. For example, in learning a spam filter, we should have good amount of data which corresponds to emails which are spam and non spam.

This even distribution is not always possible. Let’s take an example of fraud detection. Fraud detection is a use case, where by looking at transaction we need to decide is the transaction is fraudulent or not. In majority of the cases, the transaction will be normal. So the data for fraudulent data is very small compared to normal ones. In these cases, there will be imbalance in target labels. This will effect the quality of models we can build.So in next series of posts we will discuss about what’s class imbalance and how to handle it in python and spark.

This is the second post in the series where we discuss about handling class imbalance using undersampling technique. You can read all the blogs in the series [here](/categories/class-imbalance).


## Undersampling

Undersampling is one of the techniques used for handling class imbalance. In this technique, we under sample majority class to match
the minority class. So in our example, we take random sample of non-fraud class to match number of fraud samples. This makes sure that
the training data has equal amount of fraud and non-fraud samples.

## Undersampling in Python

The below is the code to do the undersampling in python.

### 1. Find Number of samples which are Fraud

{% highlight python %}
no_frauds = len(df[df['Class'] == 1])
{% endhighlight %}

### 2.  Get indices of non fraud samples

{% highlight python %}
non_fraud_indices = df[df.Class == 0].index
{% endhighlight %}

### 3. Random sample non fraud indices

{% highlight python %}
random_indices = np.random.choice(non_fraud_indices,no_frauds, replace=False)
{% endhighlight %}

### 4. Find the indices of fraud samples

{% highlight python %}
fraud_indices = df[df.Class == 1].index
{% endhighlight %}

### 5. Concat fraud indices with sample non-fraud ones

{% highlight python %}
under_sample_indices = np.concatenate([fraud_indices,random_indices])
{% endhighlight %}

### 6. Get Balance Dataframe

{% highlight python %}
under_sample = df.loc[under_sample_indices]
{% endhighlight %}

## Visualising Undersampled Data

The below is the class distribution of *under_sample* dataframe.

![Imbalance Plot](/images/under_sample_class_plot.png)

In the above plot, you can observe that classes are distributed evenly now.


## Running Logistic Regression on Undersampled Data

Once we have undersampled data, we need to train on that.

### Training

The below code runs logistic regression on undersampled data.

{% highlight python %}
X_under = under_sample.loc[:,under_sample.columns != 'Class']
y_under = under_sample.loc[:,under_sample.columns == 'Class']
X_under_train, X_under_test, y_under_train, y_under_test = train_test_split(X_under,y_under,test_size = 0.3, random_state = 0)

lr_under = LogisticRegression()
lr_under.fit(X_under_train,y_under_train)
y_under_pred = lr_under.predict(X_under_test)
{% endhighlight %}


### Results

Once we run trained the model, we can verify the model using accuracy and recall scores as we did in last post.


{% highlight python %}
print(recall_score(y_under_test,y_under_pred))
print(accuracy_score(y_under_test,y_under_pred))
{% endhighlight %}

The result is 

{% highlight text %}
0.925170068027
0.952702702703
{% endhighlight %}

As you can observe from the result, our recall has improved a lot. It was 61% percent when data was unbalanced but now it's 92%. This means our model is pretty good identifying the fraud.

Accuracy score has gone down because we undersampled data. This is fine in our case because if we miss classify some non-fraud transactions as fraud it doesn't do any harm.

## Generalisation

Whenever we undersample data, the training data size reduces significantly. So even though model works well for balanced data, we need to make sure does it generalise well. The below code calculates score for full data using above model


{% highlight python %}
y_pred_full = lr_under.predict(X_test)
print(recall_score(y_test,y_pred_full))
print(accuracy_score(y_test,y_pred_full))
{% endhighlight %}

The result is


{% highlight text %}
0.925170068027
0.968212726613
{% endhighlight %}

As you can observe from the results, accuracy score is still good for when we predict for unbalanced data. This makes sure that our model generalises well even if it's trained on undersample data.


## Using Class Weight 

In above code, we did class imbalance explicitly. But scikit-learn logistic regression has a option named *class_weight* when specified does class imbalance handling implicitly. 

The below code shows how to do the same

{% highlight python %}
lr_balanced = LogisticRegression(class_weight = 'balanced')
lr_balanced.fit(X_train,y_train)
y_balanced_pred = lr_balanced.predict(X_test)
print(recall_score(y_test,y_balanced_pred))
print(accuracy_score(y_test,y_balanced_pred))
{% endhighlight %}

The result is

{% highlight text %}
0.91156462585
0.977177767635
{% endhighlight %}

You can access complete code from python notebook from [github](https://github.com/phatak-dev/spark-ml-kaggle/blob/master/python/credit_card_class_imbalance.ipynb) or live notebook on [kaggle](https://www.kaggle.com/madhukaraphatak/under-sampling-to-achieve-better-recall).


## Conclusion

In this post we understood how to handle class imbalance using undersampling technique.

## What's Next?

In our next post, we will discuss how to implement undersampling in spark.


