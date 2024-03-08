package csv.checker.csvmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "old_csv", indexes = {
    @Index(columnList = "csvId"),
    @Index(columnList = "numero"),
    @Index(columnList = "rep"),
    @Index(columnList = "nom_afnor"),
    @Index(columnList = "code_postal"),
    @Index(columnList = "nom_commune"),
})
@AllArgsConstructor
@NoArgsConstructor
public class OldCsv {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	
    @Column(name = "CSV_ID")
    private String csvId;
    
    @Column(name = "ID_FANTOIR")
    private String idFantoir;
    
    @Column(name = "NUMERO")
    private String numero;
    
    @Column(name = "REP")
    private String rep;
    
    @Column(name = "NOM_VOIE")
    private String nomVoie;
    
    @Column(name = "CODE_POSTAL")
    private String codePostal;
    
    @Column(name = "CODE_INSEE")
    private String codeInsee;
    
    @Column(name = "NOM_COMMUNE")
    private String nomCommune;  

    @Column(name = "CODE_INSEE_ANCIENNE_COMMUNE")
    private String codeInseeAncienneCommune;
    
    @Column(name = "NOM_ANCIENNE_COMMUNE")
    private String nomAncienneCommune;
    
    @Column(name = "X")
    private String x;
    
    @Column(name = "Y")
    private String y;
    
    @Column(name = "LON")
    private Double lon;
    
    @Column(name = "LAT")
    private Double lat;
    
    @Column(name = "TYPE_POSITION")
    private String typePosition;
    
    @Column(name = "CSV_ALIAS")
    private String csvAlias;
    
    @Column(name = "NOM_LD")
    private String nomLd;
    
    @Column(name = "LIBELLE_ACHEMINEMENT")
    private String libelleAcheminement;
    
    @Column(name = "TYPE")
    private String type = "old";
    
    @Column(name = "NOM_AFNOR")
    private String nomAfnor;
    
    @Column(name = "SOURCE_POSITION")
    private String sourcePosition;
    
    @Column(name = "SOURCE_NOM_VOIE")
    private String sourceNomVoie;
    
    @Column(name = "CERTIFICATION_CO")
    private String certificationCo;
    
    @Column(name = "ITEM_NO", length = 1024)
    private String itemNo;
    
    @Column(name = "GPS_UPDATED")
    private Boolean gpsUpdated;
    
    @Column(name = "NORD_LAT")
    private Double nordLat;
    
    @Column(name = "NORD_LON")
    private Double nordLon;
    
    public void setLat(Double lat) {
        this.lat = Math.round(lat * 1000.0) / 1000.0;
    }
    
	public void setLon(Double lon) {
	    this.lon = Math.round(lon * 100000.0) / 100000.0;
	}
}
