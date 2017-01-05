package com.intel.ssg.bdt.nlp

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by ML on 2016/11/8.
  */
object DistCRF {

  def distTrain(featureData: Array[Array[Array[Array[Int]]]],
                goldenLabel: Array[Array[Int]],
                numClasses: Int,
                numFeature: Int,
                numOfNodeFeature: Int): Array[Double] = {
    val conf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val featureRDD = sc.parallelize(featureData)
    val labelRDD = sc.parallelize(goldenLabel)

    val model1AndWeight = CRF.train(featureRDD, labelRDD, numClasses, numFeature, numOfNodeFeature)
    model1AndWeight._2
  }

}
