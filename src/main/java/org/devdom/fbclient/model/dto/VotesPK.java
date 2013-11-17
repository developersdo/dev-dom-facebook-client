package org.devdom.fbclient.model.dto;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Carlos Vásquez Polanco
 */
@Embeddable
public class VotesPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "fb_option_id")
    private long fbOptionId;
    @Basic(optional = false)
    @Column(name = "fb_uid")
    private long fbUid;

    public VotesPK() {
    }

    public VotesPK(long fbOptionId, long fbUid) {
        this.fbOptionId = fbOptionId;
        this.fbUid = fbUid;
    }

    public long getFbOptionId() {
        return fbOptionId;
    }

    public void setFbOptionId(long fbOptionId) {
        this.fbOptionId = fbOptionId;
    }

    public long getFbUid() {
        return fbUid;
    }

    public void setFbUid(long fbUid) {
        this.fbUid = fbUid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fbOptionId;
        hash += (int) fbUid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VotesPK)) {
            return false;
        }
        VotesPK other = (VotesPK) object;
        if (this.fbOptionId != other.fbOptionId) {
            return false;
        }
        if (this.fbUid != other.fbUid) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.devdom.fbclient.model.VotesPK[ fbOptionId=" + fbOptionId + ", fbUid=" + fbUid + " ]";
    }
    
}
