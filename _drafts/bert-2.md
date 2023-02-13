

## Model Training

Once the tokenization is done, we are now ready to train the model.


### Download Model

As we did with tokenizer, we will download the model using hugging face library.  

{% highlight python %}

from transformers import AutoModelForSequenceClassification
model = AutoModelForSequenceClassification.from_pretrained('bert-base-uncased',num_labels=2)

{% endhighlight %}


The above downloads a dummy sequence classification model head which needs to be tuned with data. You can read about head and body of pretrained model in below link

## TODO: Link for the body and head.



