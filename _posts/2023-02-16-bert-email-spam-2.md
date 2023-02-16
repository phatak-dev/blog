---
layout: post
title: "Email Spam Detection using Pre-Trained BERT Model : Part 2 - Model Fine Tuning"
date : 2023-02-16
categories: python transformer-models bert-email-spam
---
Recently I have been looking into Transformer based machine learning models for natural language tasks. The field of NLP has changed tremendously in the last few years and I have been fascinated by the new architectures and tools that come out at the same time. Transformer models are one such architecture.

As the frameworks and tools to build transformer models keep evolving, the documentation often becomes stale and blog posts are often confusing. So for any one topic, you may find multiple approaches which can confuse beginners.

So as I am learning these models, I am planning to document the steps to do a few of the essential tasks in the simplest way possible. This should help any beginner like me to pick up transformer models.

In this two-part series, I will be discussing how to train a simple model for email spam classification using a pre-trained transformer BERT model. This is the second post in the series where I will be discussing fine-tuning the model for spam detection. You can read all the posts in the series [here](/categories/bert-email-spam).


## Data Preparation and Tokenization

Please make sure you have gone through the first part of the series where we discussed about how to prepare our data using bert tokenization. You can find the same in the below link.

[Email Spam Detection using Pre-Trained BERT Model: Part 1 - Introduction and Tokenization](/bert-email-spam-1).

## Model Fine Tuning 

Once the tokenization is done, we are now ready to fine-tune the model.

A pre-trained model comes with a body and head. In most of the use cases, we only retrain the head part of the model. So that's why we call it fine-tuning rather than retraining. You can read more about the head and body of a transformer model at the below link.

[https://huggingface.co/course/chapter1/4](https://huggingface.co/course/chapter1/4).

## 1.Download Model

As we did with the tokenizer, we will download the model using hugging face library.  

{% highlight python %}

from transformers import AutoModelForSequenceClassification
model = AutoModelForSequenceClassification.from_pretrained('bert-base-uncased',num_labels=2)

{% endhighlight %}

The above downloads a dummy sequence classification model head which needs to be tuned with data.


## 2. Training Arguments

Training arguments are where you set various options for given model training. For simplicity, we are going to use default ones.

{% highlight python %}

from transformers import TrainingArguments,Trainer
training_args = TrainingArguments(output_dir="test_trainer")

{% endhighlight %}

## 3. Evaluation Metrics

For our training, we are going to use accuracy as an evaluation metric. The below code sets up a method to calculate the same from the model.

{% highlight python %}
import numpy as np
import evaluate
metric = evaluate.load("accuracy")


def compute_metrics(eval_pred):
    logits, labels = eval_pred
    predictions = np.argmax(logits, axis=-1)
    return metric.compute(predictions=predictions, references=labels)

{% endhighlight %}

In the above code, **np.argmax** line converts logits returned from model prediction to labels so that, they can be compared with actual labels.

## 4. Trainer

Let's create trainer with below code.

{% highlight python %}

trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_datasets_train,
    eval_dataset=tokenized_datasets_eval,
    compute_metrics=compute_metrics,
 )
 
{% endhighlight %}

Trainer API of hugging face handles all the batching and looping needed for fine-tuning the model.

## 5. Run the Train

Once trainer object is created, we can run the train the model using **train** method call.

{% highlight python %}

trainer.train()

{% endhighlight %}


## Find Accuracy on Testing Dataset

Once the model is trained, we can find how well our model is doing using accuracy on test dataset.

{% highlight python %}

predictions_output = trainer.predict(tokenizer_datasets_test)

{% endhighlight %}

In above code, we are using **trainer.predict** method to predict on our test dataset.

{% highlight python %}

accuracy_score = compute_metrics((predictions_output.predictions,tokenizer_datasets_test['label']))
print(accuracy_score)

{% endhighlight %}

Then we find the accuracy score using same function we defined at the time of train. The output will be

{% highlight python %}

{'accuracy': 0.97}

{% endhighlight %}

As you can see we are getting 97% accuracy which is really good.

## Code

Complete code for the post is in below google colab notebook.

[https://colab.research.google.com/drive/1d3-22HNkuLCqP1ctemMaMw1ETaWL48ET?usp=sharing](https://colab.research.google.com/drive/1d3-22HNkuLCqP1ctemMaMw1ETaWL48ET?usp=sharing).

You can also access python notebook on github.

[https://github.com/phatak-dev/transformer-models/blob/main/Spam_Detection_using_BERT_Model.ipynb](https://github.com/phatak-dev/transformer-models/blob/main/Spam_Detection_using_BERT_Model.ipynb).

## Conclusion

In this post, we saw how to fine-tune a pre-trained model using hugging face API. These two posts give you end to end flow of fine-tuning a transformer model.
