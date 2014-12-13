package com.madhukaraphatak.mesos.shuffle

/**
 * Created by madhu on 28/10/14.
 */
trait Shuffle[K,V,C]{
  def compute(list: List[List[(K, V)]],
  createCombiner: V => C,
  mergeValue: (C, V) => C,
  mergeCombiners: (C, C) => C,
  numberOfOutputSplit: Int,
  scheduler: TaskScheduler):List[(K,C)]
}
