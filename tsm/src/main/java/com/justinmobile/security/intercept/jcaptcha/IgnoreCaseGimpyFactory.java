package com.justinmobile.security.intercept.jcaptcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class IgnoreCaseGimpyFactory extends GimpyFactory {

	public IgnoreCaseGimpyFactory(WordGenerator generator, WordToImage word2image) {
		super(generator, word2image);
	}

	@Override
	public ImageCaptcha getImageCaptcha(Locale locale) {
		// length
		Integer wordLength = getRandomLength();

		String word = getWordGenerator().getWord(wordLength, locale);

		BufferedImage image = null;
		try {
			image = getWordToImage().getImage(word);
		} catch (Throwable e) {
			throw new CaptchaException(e);
		}

		ImageCaptcha captcha = new IgnoreCaseGimpy(CaptchaQuestionHelper.getQuestion(locale, BUNDLE_QUESTION_KEY), image, word);
		return captcha;
	}

}
