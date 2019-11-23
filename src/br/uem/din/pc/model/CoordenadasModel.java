/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uem.din.pc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luiz Flávio
 */
public class CoordenadasModel {
    List<Integer> coordenada = new ArrayList<>();
    Integer idCentroide;
    Double distanciaCentroid;
    
    public CoordenadasModel(){
        this.idCentroide = -1;
        this.distanciaCentroid = Double.MAX_VALUE;
    }
    
    public List<Integer> getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(List<Integer> coordenada) {
        this.coordenada = coordenada;
    }

    public Integer getIdCentroide() {
        return idCentroide;
    }

    public void setIdCentroide(Integer idCentroide) {
        this.idCentroide = idCentroide;
    }

    public Double getDistanciaCentroid() {
        return distanciaCentroid;
    }

    public void setDistanciaCentroid(Double distanciaCentroid) {
        this.distanciaCentroid = distanciaCentroid;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.coordenada);
        hash = 79 * hash + Objects.hashCode(this.idCentroide);
        hash = 79 * hash + Objects.hashCode(this.distanciaCentroid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CoordenadasModel other = (CoordenadasModel) obj;
        if (!Objects.equals(this.coordenada, other.coordenada)) {
            return false;
        }
        if (!Objects.equals(this.idCentroide, other.idCentroide)) {
            return false;
        }
        return Objects.equals(this.distanciaCentroid, other.distanciaCentroid);
    }
}
