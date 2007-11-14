package org.apache.struts2.rest.example;

public class Order {
    String id;
    String clientName;
    int amount;
    
    public Order() {}
    
    public Order(String id, String clientName, int amount) {
        super();
        this.id = id;
        this.clientName = clientName;
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + amount;
        result = prime * result
                + ((clientName == null) ? 0 : clientName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Order other = (Order) obj;
        if (amount != other.amount)
            return false;
        if (clientName == null) {
            if (other.clientName != null)
                return false;
        } else if (!clientName.equals(other.clientName))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    
    
    
}
