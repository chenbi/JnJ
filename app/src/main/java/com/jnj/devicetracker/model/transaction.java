
package com.jnj.devicetracker.model;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class transaction extends RealmObject
{

  public String getUid () {
    return uid;
  }

  public void setUid (String uid) {
    this.uid=uid;
  }

  @PrimaryKey
  private String uid;

  public void setObjectId (String objectId) {
    this.objectId=objectId;
  }

  private String objectId;

  private String type;
  private String ownerId;
  private String relationship;
  private String currency;
  private String method;
  private java.util.Date updated;
  private Double tax;
  private String item;
  private Double amount;
  private java.util.Date created;
  private java.util.Date time;

  final static public String UID ="uid";

  final static public String OBJECT_ID= "objectId";
  final static public String TYPE  = "type";
  final static public String RELATIONSHIP=  "relationship";
  final static public String CURRENCY=   "currency";
  final static public String METHOD="method";
  final static public String TAX="tax";
  final static public String ITEM="item";
  final static public String AMOUNT="amount";
  final static public String TIME = "time";






  public String getType()
  {
    return type;
  }

  public void setType( String type )
  {
    this.type = type;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

  public String getRelationship()
  {
    return relationship;
  }

  public void setRelationship( String relationship )
  {
    this.relationship = relationship;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency( String currency )
  {
    this.currency = currency;
  }

  public String getObjectId()
  {
    return objectId;
  }

  public String getMethod()
  {
    return method;
  }

  public void setMethod( String method )
  {
    this.method = method;
  }

  public java.util.Date getUpdated()
  {
    return updated;
  }

  public Double getTax()
  {
    return tax;
  }

  public void setTax( Double tax )
  {
    this.tax = tax;
  }

  public String getItem()
  {
    return item;
  }

  public void setItem( String item )
  {
    this.item = item;
  }

  public Double getAmount()
  {
    return amount;
  }

  public void setAmount( Double amount )
  {
    this.amount = amount;
  }

  public java.util.Date getCreated()
  {
    return created;
  }

  public java.util.Date getTime()
  {
    return time;
  }

  public void setTime( java.util.Date time )
  {
    this.time = time;
  }

                                                    
  public transaction save()
  {
    return Backendless.Data.of( transaction.class ).save( this );
  }

  public void saveAsync( AsyncCallback<transaction> callback )
  {
    Backendless.Data.of( transaction.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( transaction.class ).remove( this );
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( transaction.class ).remove( this, callback );
  }

  public static transaction findById( String id )
  {
    return Backendless.Data.of( transaction.class ).findById( id );
  }

  public static void findByIdAsync(String id, AsyncCallback<transaction> callback )
  {
    Backendless.Data.of( transaction.class ).findById( id, callback );
  }

  public static transaction findFirst()
  {
    return Backendless.Data.of( transaction.class ).findFirst();
  }

  public static void findFirstAsync( AsyncCallback<transaction> callback )
  {
    Backendless.Data.of( transaction.class ).findFirst( callback );
  }

  public static transaction findLast()
  {
    return Backendless.Data.of( transaction.class ).findLast();
  }

  public static void findLastAsync( AsyncCallback<transaction> callback )
  {
    Backendless.Data.of( transaction.class ).findLast( callback );
  }

  public static List<transaction> find(DataQueryBuilder queryBuilder )
  {
    return Backendless.Data.of( transaction.class ).find( queryBuilder );
  }

  public static void findAsync( DataQueryBuilder queryBuilder, AsyncCallback<List<transaction>> callback )
  {
    Backendless.Data.of( transaction.class ).find( queryBuilder, callback );
  }
}