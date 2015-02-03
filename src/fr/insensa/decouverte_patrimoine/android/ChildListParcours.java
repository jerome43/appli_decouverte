package fr.insensa.decouverte_patrimoine.android;

public class ChildListParcours
{
    private String titre;
    private String description;
    private String uri;
    private String numeroParcours;
    private String keyId;
    private String zipName;
    private String departement;

    public String getTitre()
    {
        return titre;
    }

    public void setTitre(String titre)
    {
        this.titre = titre;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUri()    {  return uri;  }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getNumeroParcours()
    {
        return numeroParcours;
    }

    public void setNumeroParcours (String numeroParcours)
    {
        this.numeroParcours = numeroParcours;
    }

    public String getKeyId()    {  return keyId;  }

    public void setKeyId(String keyId)
    {
        this.keyId = keyId;
    }

    public String getZipName()    {  return zipName;  }

    public void setZipName(String zipName)
    {
        this.zipName = zipName;
    }

    public String getDepartement()    {  return departement;  }

    public void setDepartement(String departement)
    {
        this.departement = departement;
    }
}