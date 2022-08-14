package com.syakeapps.sprandoom3.view.bean;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.syakeapps.sprandoom3.jpa.bean.Weapon;
import com.syakeapps.sprandoom3.jpa.bean.WeaponClass;

import lombok.Getter;
import lombok.Setter;
import net.bootsfaces.utils.FacesMessages;

@SuppressWarnings("serial")
@SessionScoped
@Named(value = "IDX_BB")
public class IndexBackingBean implements Serializable {

    @Inject
    private ApplicationContext context;

    @PersistenceContext
    private EntityManager em;

    private ResourceBundle bundle;

    /* Randomizer User Setting */
    @Getter(onMethod_ = { @lombok.Generated })
    @Setter(onMethod_ = { @lombok.Generated })
    private String selectedClassIds;
    @Getter(onMethod_ = { @lombok.Generated })
    @Setter(onMethod_ = { @lombok.Generated })
    private int selectedSubId = 0;
    @Getter(onMethod_ = { @lombok.Generated })
    @Setter(onMethod_ = { @lombok.Generated })
    private int selectedSpecialId = 0;

    /* Randomized Weapon */
    SecureRandom rand;
    @Getter(onMethod_ = { @lombok.Generated })
    private Weapon pickupedWeapon;

    @PostConstruct
    public void postConstruct() throws NoSuchAlgorithmException {
        bundle = ResourceBundle.getBundle("locale.Messages",
                FacesContext.getCurrentInstance().getViewRoot().getLocale(),
                Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));

        List<WeaponClass> classes = context.getClasses();
        selectedClassIds = classes.stream().map(c -> String.valueOf(c.getId())).collect(Collectors.joining(","));

        rand = SecureRandom.getInstanceStrong();
        pickupedWeapon = context.getWeapons().get(0);

        FacesMessages.info(bundle.getString("MSG_INFO_INITIALIZED"));
    }

    @Transactional
    public void randomizeWeapon() {
        /* class setting check */
        if (selectedClassIds == null || selectedClassIds.isEmpty()) {
            FacesMessages.warning(bundle.getString("MSG_WARN_CLASS_IS_REQUIRED"));
            return;
        }

        /* filtering by settings */
        List<Weapon> candidates = createCandidates();

        /* size check */
        if (candidates.isEmpty()) {
            FacesMessages.warning(bundle.getString("MSG_WARN_NO_WEAPON_HIT"));
            return;
        }

        /* pick up a random weapon */
        pickupedWeapon = pickupRandom(candidates);
    }

    private List<Weapon> createCandidates() {
        List<Weapon> candidate = new ArrayList<>();

        List<Integer> convertedIds = Arrays.asList(selectedClassIds.split(",")).stream().map(Integer::valueOf).toList();

        if (convertedIds.size() == context.getClasses().size()) {
            candidate = context.getWeapons();
        } else {
            List<WeaponClass> classes = em.createNamedQuery(WeaponClass.FIND_BY_IDS, WeaponClass.class)
                    .setParameter("ids", convertedIds).getResultList();

            for (int i = 0; i < classes.size(); i++) {
                candidate.addAll(classes.get(i).getWeapons());
            }
        }

        /* filtering by sub */
        if (selectedSubId != 0) {
            candidate = candidate.stream().filter(w -> w.getSub().getId() == selectedSubId)
                    .collect(Collectors.toList());
        }

        /* filtering by special */
        if (selectedSpecialId != 0) {
            candidate = candidate.stream().filter(w -> w.getSpecial().getId() == selectedSpecialId)
                    .collect(Collectors.toList());
        }

        return candidate;
    }

    private <T> T pickupRandom(List<T> candidates) {
        return candidates.get(rand.nextInt(candidates.size()));
    }

    /* GETTER & SETTER */
    public Locale getUserLocale() {
        return bundle.getLocale();
    }
}
