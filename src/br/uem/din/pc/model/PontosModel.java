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
public class PontosModel {
    List<CoordenadasModel> pontos = new ArrayList<>();

    public List<CoordenadasModel> getPontos() {
        return pontos;
    }

    public void setPontos(List<CoordenadasModel> pontos) {
        this.pontos = pontos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.pontos);
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
        final PontosModel other = (PontosModel) obj;
        return Objects.equals(this.pontos, other.pontos);
    }
}
