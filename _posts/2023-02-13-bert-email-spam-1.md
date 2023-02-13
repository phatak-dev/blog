---
layout: post
title: "Email Spam Detection using Pre-Trained BERT Model : Part 1 - Introduction and Tokenization"
date : 2023-02-13
categories: python transformer-models bert-email-spam
---
Recently I have been looking into Transformer based machine learning models for natural language tasks. The field of NLP has changed tremendously in last few years and I have been fascinated by the new architectures and tools that they are come out in same time. Transformer models is one of such architecture.  

As the frameworks and tools to build transformer models keeps evolving, the documentation often become stale and blog posts often confusing. So for any one topic, you may find multiple approaches which can confuse beginner.

So as I am learning these models, I am planning to document the steps to do few of the important tasks in simplest way possible. This should help any beginner like me to pickup transformer models.

In this two part series, I will be discussing about  how to train a simple model for email spam classification using pre trained transformer BERT model.This is the first post in series where I will be discussing about transformer models and preparing our data. You can read all the posts in the series [here](/categories/bert-email-spam).

## Transformer Models

Transformer is a neural network architecture first introduced by Google in 2017. This architecture has proven extremely efficient in learning various tasks. Some of the popular models of transformer architecture is BERT, Distilbert, GPT-3, chatGPT etc.

You can read more about transformer models in below link 


[https://huggingface.co/course/chapter1/4](https://huggingface.co/course/chapter1/4).

## Pre-Trained Language Model and Transfer Learning

A pre-trained language model is a transformer model, which is trained on large amount of language data for specific tasks.

The idea behind using pre-trained model is that, model has really good understand of language which we can borrow for our nlp task as it is and just focus on training unique part of task in our model. This is called as transfer learning. You can read more about transfer learning in below link

[https://huggingface.co/course/chapter1/4#transfer-learning](https://huggingface.co/course/chapter1/4#transfer-learning).

## Google Colab

Google Colab is a hosted jupyter python notebook which has access GPU runtime. As these transformer models perform extremely well on GPU, we are going to use google colab for our examples. You can get community version of same by signing in using your google credentials.

## Installing Libraries

First step to install libraries. These libraries come from huggingface, a company that provides tools for simplifying building transformer based models.

{% highlight python %}

!pip install datasets
!pip install transformers
!pip install evaluate

{% endhighlight %}

In above

 * *transformer* library provides all the pre trained models and tools to train a model
 * *datasets* library provides tool to load and use datasets in form required by above models
 * *evaluate* a helper library to calculate metrics for training


## Email Spam Data and Preparation

In this section of the post, we will be discussing about our spam data and it's preparation.


### 1. Spam Data

For our example, we are going to use the email spam data from below link

[https://www.kaggle.com/datasets/balaka18/email-spam-classification-dataset-csv](https://www.kaggle.com/datasets/balaka18/email-spam-classification-dataset-csv)

The data has two important fields 

1. v2 - Content of Email
2. v1 - Label which indicates spam or not

**Please download data from kaggle and upload to your instance of google colab.**

### 2. Loading Data to Dataframe

{% highlight python %}
import pandas as pd
# data from https://www.kaggle.com/datasets/balaka18/email-spam-classification-dataset-csv
file_path="spam.csv"
df = pd.read_csv(file_path,encoding = "ISO-8859-1")

df = df[["v1","v2"]]

{% endhighlight %}


### 3. Mapping the Labels

In the data, labels are "ham" and "spam". We need to map them to 0 and 1. The below code does the same.

{% highlight python %}

df['label'] = df.v1.map({'ham':0, 'spam':1})

{% endhighlight %}

### 4. Generating Different Datasets

Once we have mapped labels, we will be creating train, test and validate sets.

{% highlight python %}

from datasets import Dataset
train_data_df = df.sample(200, random_state=42)
eval_data_df = df.sample(200, random_state=45)
train_dataset = Dataset.from_pandas(train_data_df)
eval_dataset = Dataset.from_pandas(eval_data_df)
test_data_df = df.iloc[4000:4100]
test_dataset = Dataset.from_pandas(test_data_df)

{% endhighlight %}


**In the above code, we use the *Dataset.from_pandas* to create hugging face compatible datasets which will be using in next steps.**

## Tokenization

To use any pre-trained model, one of the pre requisites is that we need to use tokenization of the model on our dataset. This will make sure that the model can take our data as input.


### 1. Download Tokenizer

First step in the tokenization is to download right tokenization model.

{% highlight python %}

from transformers import AutoTokenizer
tokenizer = AutoTokenizer.from_pretrained("bert-base-uncased")

{% endhighlight %}

Hugging face transformer library provides the helper class called AutoTokenizer. This class provides method *from_pretrained* which will help to download the tokenization model from hugging face repository. The model we are using base bert model trained on uncased data.

### 2. Tokenize Datasets

Once tokenizer is downloaded and ready to use, we can tokenize our datasets.

{% highlight python %}
def tokenize_function(examples):
    return tokenizer(examples["v2"], padding="max_length", truncation=True)

tokenized_datasets_train = train_dataset.map(tokenize_function, batched=True)
tokenized_datasets_eval = eval_dataset.map(tokenize_function, batched=True)
tokenizer_datasets_test = test_dataset.map(tokenize_function, batched=True)

{% endhighlight %}

In above code, we use *tokenize_function* which selects the right column which has the text data. Then using the *map* function tokenization will be applied for each batch.


## Code

Complete code for the post is in below google colab notebook.

[https://colab.research.google.com/drive/1d3-22HNkuLCqP1ctemMaMw1ETaWL48ET?usp=sharing](https://colab.research.google.com/drive/1d3-22HNkuLCqP1ctemMaMw1ETaWL48ET?usp=sharing).

You can also access python notebook on github.

[https://github.com/phatak-dev/transformer-models/blob/main/Spam_Detection_using_BERT_Model.ipynb](https://github.com/phatak-dev/transformer-models/blob/main/Spam_Detection_using_BERT_Model.ipynb).


## Conclusion
In this post, we understood what are transformation models. We also prepared our dataset to have model tokenization. In the next post, we will see how to fine tune the model. 
