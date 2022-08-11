package com.syakeapps.sprandoom3.view.bean;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.syakeapps.sprandoom3.jpa.bean.Special;
import com.syakeapps.sprandoom3.jpa.bean.Sub;
import com.syakeapps.sprandoom3.jpa.bean.Weapon;
import com.syakeapps.sprandoom3.jpa.bean.WeaponClass;

@ApplicationScoped
@Named(value = "APP")
public class ApplicationContext {
    private static final Logger LOGGER = Logger.getLogger(ApplicationContext.class.getName());

    @PersistenceContext
    private EntityManager em;

    /* CONST */
    private List<Weapon> weapons;
    private List<WeaponClass> classes;
    private List<Sub> subs;
    private List<Special> specials;

    @PostConstruct
    private void initialize() {
        LOGGER.info("Initializing application.");
        weapons = em.createNamedQuery(Weapon.FIND_ALL, Weapon.class).getResultList();
        classes = em.createNamedQuery(WeaponClass.FIND_ALL, WeaponClass.class).getResultList();
        subs = em.createNamedQuery(Sub.FIND_ALL, Sub.class).getResultList();
        specials = em.createNamedQuery(Special.FIND_ALL, Special.class).getResultList();
        LOGGER.info("Initializing application is done.");
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<WeaponClass> getClasses() {
        return classes;
    }

    public List<Sub> getSubs() {
        return subs;
    }

    public List<Special> getSpecials() {
        return specials;
    }
}
