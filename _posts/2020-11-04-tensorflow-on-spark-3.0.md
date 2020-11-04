---
layout: post
title: "Distributed TensorFlow on Apache Spark 3.0"
date : 2020-11-04
categories: python spark spark-three 
---
TensorFlow is a popular deep learning framework used across the industry. TensorFlow supports the distributed training on a CPU or GPU cluster. This distributed training allows users to run it on a large amount of data with lot of deep layers.

## TensorFlow Integration with Apache Spark 2.x

Currently if we want to use the TensorFlow with Apache Spark, we need to do all ETL needed for TensorFlow in pyspark and write data to intermediate storage. Then that data needs to be loaded to the TensorFlow cluster to do the actual training. This makes user to maintain two different clusters one for ETL and one for distributed training of TensorFlow. Running and maintaining multiple separate clusters is tedious. But this is going to change from the Apache Spark 3.0.

## Project Hydrogen - Deep Learning Framework Support in Apache Spark 

In Spark 3.0, with project hydrogen, a native support for the deep learning frameworks is added. The center of approach is allowing non map/reduce based scheduling on the spark cluster. 

### Map/Reduce Execution Mode in Apache Spark

In Spark 2.x, spark supported only Map/Reduce based job execution. In this kind of execution, the spark program is divided into multiple stages of map and reduce. This kind of scheduling is inspired by Hadoop Map/ Reduce. This works great for many big data workloads like ETL, SQL, Normal ML etc. But this kind of scheduling is inefficient for implementing deep learning frameworks.

### Barrier Execution Mode in Spark 3.0

Spark 3.0 implements a new execution mode called barrier execution mode which is different than standard Map/Reduce model. This kind of execution mode is useful implementing the distributed deep learning in spark. 

In Map/Reduce, all tasks in a stage are independent of each other and they don't communicate to each other. If one of the task fails, only that task will be retried. 

But in Barrier execution mode, all tasks in a stage will be started together and if one of the task fails whole stage will be retried again. All those tasks can communicate to each other.

You can learn more about this execution mode from below link.

[https://issues.apache.org/jira/browse/SPARK-24374](https://issues.apache.org/jira/browse/SPARK-24374).

## TensorFlow Support in Spark 3.0

This section of post talks about different parts of TensorFlow support in spark 3.0

### Spark TensorFlow Distributor

Spark TensorFlow Distributor is a python library which implements the barrier execution mode of spark to implement distributed TensorFlow training on top of the spark 3.0 cluster. You need to install this library as part of the pyspark environment.

[https://github.com/tensorflow/ecosystem/tree/master/spark/spark-tensorflow-distributor](https://github.com/tensorflow/ecosystem/tree/master/spark/spark-tensorflow-distributor).

### Running TensorFlow on Apache Spark

{% highlight python %}

from spark_tensorflow_distributor import MirroredStrategyRunner

# Taken from https://github.com/tensorflow/ecosystem/tree/master/spark/spark-tensorflow-distributor#examples
def train():
    import tensorflow as tf
    import uuid

    BUFFER_SIZE = 10000
    BATCH_SIZE = 64

    def make_datasets():
        (mnist_images, mnist_labels), _ = \
            tf.keras.datasets.mnist.load_data(path=str(uuid.uuid4())+'mnist.npz')

        dataset = tf.data.Dataset.from_tensor_slices((
            tf.cast(mnist_images[..., tf.newaxis] / 255.0, tf.float32),
            tf.cast(mnist_labels, tf.int64))
        )
        dataset = dataset.repeat().shuffle(BUFFER_SIZE).batch(BATCH_SIZE)
        return dataset

    def build_and_compile_cnn_model():
        model = tf.keras.Sequential([
            tf.keras.layers.Conv2D(32, 3, activation='relu', input_shape=(28, 28, 1)),
            tf.keras.layers.MaxPooling2D(),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(64, activation='relu'),
            tf.keras.layers.Dense(10, activation='softmax'),
        ])
        model.compile(
            loss=tf.keras.losses.sparse_categorical_crossentropy,
            optimizer=tf.keras.optimizers.SGD(learning_rate=0.001),
            metrics=['accuracy'],
        )
        return model

    train_datasets = make_datasets()
    options = tf.data.Options()
    options.experimental_distribute.auto_shard_policy = tf.data.experimental.AutoShardPolicy.DATA
    train_datasets = train_datasets.with_options(options)
    multi_worker_model = build_and_compile_cnn_model()
    multi_worker_model.fit(x=train_datasets, epochs=3, steps_per_epoch=5)

MirroredStrategyRunner(num_slots=8).run(train)

{% endhighlight %}

The above code shows a simple TensorFlow training with **spark tensorflow distributor**. 

In the above code, we import **MirroredStrategyRunner** from spark tensorflow distributor library, which implements barrier execution mode. All other code till last line is standard TensorFlow code. The last line executes train with our runner. Runner takes below configuration

  * num_slots-  Total number of GPUs or CPU only Spark tasks that participate in distributed training 
  * local_mode: If True, the training function will be run locally
                on the driver. If False training is distributed among the
                workers.
  * use_gpu - Should use gpu or not. More in next section. If it's set to false, then CPU based training is used
 

With this library, we can start using same spark cluster to train our deep learning models. No more need of a different cluster. 

## GPU Support

TensorFlow runs faster on GPU's than CPU based clusters. From Spark 3.0, we can run spark cluster on gpu based machines as we do on CPU. We can use this integration to run the TensorFlow on GPU on a GPU spark cluster. This will further speed-up the training. **use_gpu** controls that switch.

## References

[https://docs.databricks.com/applications/machine-learning/train-model/distributed-training/spark-tf-distributor.html](https://docs.databricks.com/applications/machine-learning/train-model/distributed-training/spark-tf-distributor.html).

## Conclusion

Spark 3.0 natively support running deep learning frameworks on it's cluster. This native integration helps to use advanced deep learning algorithms on big data environments. 
