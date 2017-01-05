package com.intel.ssg.bdt.nlp

import java.io._

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by ML on 2016/11/8.
  */
object CRFMemTest {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}").setMaster("local[2]")
    val sc = new SparkContext(conf)


    /*val ois = new ObjectInputStream(new FileInputStream("weight.obj"))
    val weight = ois.readObject.asInstanceOf[Array[Double]]
    ois.close()*/

    /*val ois: ObjectInputStream = new ObjectInputStream(new FileInputStream("featureData.obj"))
    val featureData: Array[Array[Array[Array[Int]]]] = ois.readObject.asInstanceOf[Array[Array[Array[Array[Int]]]]]
    val goldenLabel: Array[Array[Int]] = ois.readObject.asInstanceOf[Array[Array[Int]]]
    val numClasses: Int = ois.readObject.asInstanceOf[Int]
    val numFeature = ois.readObject().asInstanceOf[Int]
    val numOfNodeFeature = ois.readObject().asInstanceOf[Int]

    val featureRDD = sc.parallelize(featureData)
    val labelRDD = sc.parallelize(goldenLabel)

    val model1AndWeight = CRF.train(featureRDD, labelRDD, numClasses, numFeature, numOfNodeFeature)
    val oos = new ObjectOutputStream(new FileOutputStream("weight.obj"))
    oos.writeObject(model1AndWeight._2)
    oos.close()*/

    // Read train data file and template file , then train model with them
    if (args(0).equals("learn")) {
      val templateFile = args(1)
      val trainFile = args(2)
      val testFile = args(3)
      var template = new ArrayBuffer[String]
      var trainContent = new ArrayBuffer[String]
      for (line<- Source.fromFile(templateFile).getLines()) {
        if (line.startsWith("U") || line.startsWith("B")) {
          val fields = line.replace("\n", "")
          template.append(fields)
        }
      }
      var sentence = new ArrayBuffer[String]
      for (line<- Source.fromFile(trainFile).getLines()) {
        val fields = line.replace("\n", "")
        if (fields.length() == 0 && sentence.nonEmpty) {
          trainContent.append(sentence.mkString("\n"))
          sentence.clear()
        } else {
          sentence.append(fields)
        }
      }
      if (sentence.nonEmpty) {
        trainContent.append(sentence.mkString("\n"))
      }
      val trainRdd2 = sc.parallelize(trainContent).map(sentence => {
        val tokens = sentence.split("\n")
        Sequence(tokens.map(token => {
          val tags: Array[String] = token.split('\t')
          Token.put(tags.last, tags.dropRight(1))
        }))
      })
      sentence.clear()
      var testContent = new ArrayBuffer[String]
      for (line<- Source.fromFile(testFile).getLines()) {
        val fields = line.replace("\n", "")
        if (fields.length() == 0 && sentence.nonEmpty) {
          testContent.append(sentence.mkString("\n"))
          sentence.clear()
        } else {
          sentence.append(fields)
        }
      }
      if (sentence.nonEmpty) {
        testContent.append(sentence.mkString("\n"))
      }
      val model2 = CRF.train(template.to[Array], trainRdd2)
      val result2 = model2.predict(testContent.to[Array].map(sentence => {
        val tokens = sentence.split("\n")
        Sequence(tokens.map(token => {
          val tags: Array[String] = token.split('\t')
          Token.put(tags)
        }))
      }))
      val writer = new PrintWriter(new File("test.out" ))
      result2.foreach({result=>
        result.toArray.foreach({token=>
          writer.println(token.tags.mkString("\t") + "\t" + token.label)
        })
        writer.println("")
      })
      writer.close()
    }
    /*val template = Array("U00:%x[-1,0]", "U01:%x[0,0]", "U02:%x[1,0]", "B")

    //val train1 = Array("B-NP|--|Friday|-|NNP\tB-NP|--|'s|-|POS", "I-NP|--|Market|-|NNP\tI-NP|--|Activity|-|NN")
    val train1 = Array("I-NP|--|Market|-|NNP\tI-NP|--|Activity|-|NN")
    val test1 = Array("null|--|Market|-|NNP\tnull|--|Activity|-|NN")
    val trainRdd1 = sc.parallelize(train1).map(Sequence.deSerializer)
    val model1 = CRF.train(template, trainRdd1)
    val result1 = model1.predict(test1.map(Sequence.deSerializer))
    val i = 0*/

   /* val train2 = Array("Friday NNP B-NP\n's POS B-NP", "Market NNP I-NP\nActivity NN I-NP")
    val test2 = Array("Market NNP\nActivity NN")
    val trainRdd2 = sc.parallelize(train2).map(sentence => {
      val tokens = sentence.split("\n")
      Sequence(tokens.map(token => {
        val tags: Array[String] = token.split(' ')
        Token.put(tags.last, tags.dropRight(1))
      }))
    })
    val model2 = CRF.train(template, trainRdd2)
    val result2 = model2.predict(test2.map(sentence => {
      val tokens = sentence.split("\n")
      Sequence(tokens.map(token => {
        val tags: Array[String] = token.split(' ')
        Token.put(tags)
      }))
    }))*/
  }

}
