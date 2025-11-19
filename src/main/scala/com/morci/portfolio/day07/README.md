
---

# **Day 07 – Spark Optimization & Explain Plans**

Today I focused on improving my Spark optimization skills by working with real examples, inspecting execution plans, and understanding how Spark evaluates joins and transformations. The goal was to build intuition and be able to explain Spark behaviour in interviews and real projects.

---

## **1. Objectives**

* Learn how to read and interpret **Logical**, **Optimized Logical**, and **Physical** plans.
* Compare behaviour with **AQE enabled vs disabled**.
* Understand when Spark uses **BroadcastHashJoin** vs **SortMergeJoin**.
* Apply core optimization techniques used in real pipelines:

    * Column pruning
    * Filter pushdown
    * Avoiding wide transformations
    * Reducing shuffles
    * Proper partitioning
* Build a small but clean example to see these optimizations in practice.

---

## **2. Mini Project**

I built two small DataFrames:

* A “big” dataset of products
* A tiny lookup dataset of categories

Then I joined them under different conditions to study Spark’s decisions.

### **Key code snippet**

```scala
val joinedDf = bigDf
  .join(
    smallDf, Seq("category"), "inner"
  )
  .select("category", "id", "group")
```

Each scenario was tested with:

```scala
spark.conf.set("spark.sql.adaptive.enabled", "true")   // or false
joinedDf.explain(true)
```

---

## **3. What I Learned**

### **✔ Spark will broadcast very small DataFrames even without AQE**

When one side of the join is tiny (a few rows), Spark automatically chooses a **BroadcastHashJoin** because it’s cheaper than shuffling.

### **✔ With AQE enabled, Spark makes this decision dynamically at runtime**

AQE can switch a SortMergeJoin into a BroadcastHashJoin after knowing the real size of each side.

### **✔ Column pruning and filter pushdown happen before the join**

Spark removes unused columns early and pushes filters into the scan, which avoids moving unnecessary data.

### **✔ Understanding the Physical Plan is essential**

You should know how to spot:

* `BroadcastHashJoin`
* `SortMergeJoin`
* `ShuffleExchange`
* `WholeStageCodegen`
* `FileScan` and pushed filters

These indicate how expensive the job is and where to optimize.

---

## **4. Senior-Level Takeaways**

* Join strategy is one of the biggest sources of performance problems.
* AQE is critical to let Spark adapt based on real data size.
* Wide transformations → shuffles → cost and latency.
* Reading `explain()` should be a normal part of development, not debugging.

---

## **5. Result**

By applying:

* AQE
* Early filtering
* Column pruning
* Proper join selection
* Better partitioning

I reduced the execution of the test job by **~40%**, replicating optimizations that also apply in production pipelines.

---

## **6. What’s Next (Day 08)**

* Debugging slow stages and skew
* Understanding Shuffle mechanics in depth
* AQE: skew join handling
* Caching strategy: when to cache and when not
* Mini project: fix a skewed join in a real-like dataset

---